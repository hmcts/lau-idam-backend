provider "azurerm" {
  features {}
}

provider "azurerm" {
  subscription_id            = var.aks_subscription_id
  skip_provider_registration = "true"
  features {}
  alias                      = "postgres_network"

}

locals {
  db_connection_options  = "?sslmode=require"
  vault_name             = "${var.product}-${var.env}"
  asp_name               = "${var.product}-${var.env}"
  env                    = var.env
}

module "lau-idam-db-flexible" {
  providers = {
    azurerm.postgres_network = azurerm.postgres_network
  }

  source = "git@github.com:hmcts/terraform-module-postgresql-flexible?ref=master"
  env = var.env

  product = var.product
  component = var.component
  business_area = "cft"
  name = "${var.product}-${var.component}-flexible"

  common_tags = var.common_tags

  pgsql_storage_mb = var.pgsql_storage_mb

  pgsql_admin_username = "lauadmin"
  pgsql_version   = "15"

  pgsql_databases = [
    {
      name: "lau_idam"
    }
  ]

  pgsql_server_configuration = [
    {
      name = "azure.extensions"
      value = "plpgsql,pg_stat_statements,pg_buffercache,pgcrypto"
    }
  ]

  admin_user_object_id = var.jenkins_AAD_objectId
}

module "lau-idam-db" {
  source                 = "git@github.com:hmcts/cnp-module-postgres?ref=master"
  product                = "${var.product}-${var.component}"
  location               = var.location_db
  env                    = var.env
  database_name          = "lau_idam"
  postgresql_user        = "lauadmin"
  postgresql_version     = "11"
  postgresql_listen_port = "5432"
  sku_name               = "GP_Gen5_2"
  sku_tier               = "GeneralPurpose"
  common_tags            = var.common_tags
  subscription           = var.subscription
}

data "azurerm_key_vault" "key_vault" {
  name                = local.vault_name
  resource_group_name = local.vault_name
}

////////////////////////////////
// Populate Vault with DB info
////////////////////////////////

resource "azurerm_key_vault_secret" "POSTGRES-USER" {
  key_vault_id = data.azurerm_key_vault.key_vault.id
  name         = "${var.component}-POSTGRES-USER"
  value        = module.lau-idam-db.user_name
}

resource "azurerm_key_vault_secret" "POSTGRES-PASS" {
  key_vault_id = data.azurerm_key_vault.key_vault.id
  name         = "${var.component}-POSTGRES-PASS"
  value        = module.lau-idam-db.postgresql_password
}

resource "azurerm_key_vault_secret" "POSTGRES_HOST" {
  key_vault_id = data.azurerm_key_vault.key_vault.id
  name         = "${var.component}-POSTGRES-HOST"
  value        = module.lau-idam-db.host_name
}

resource "azurerm_key_vault_secret" "POSTGRES_PORT" {
  key_vault_id = data.azurerm_key_vault.key_vault.id
  name         = "${var.component}-POSTGRES-PORT"
  value        = module.lau-idam-db.postgresql_listen_port
}

resource "azurerm_key_vault_secret" "POSTGRES_DATABASE" {
  key_vault_id = data.azurerm_key_vault.key_vault.id
  name         = "${var.component}-POSTGRES-DATABASE"
  value        = module.lau-idam-db.postgresql_database
}

# Copy postgres password for flyway migration
resource "azurerm_key_vault_secret" "flyway_password" {
  key_vault_id = data.azurerm_key_vault.key_vault.id
  name         = "idam-flyway-password"
  value        = module.lau-idam-db.postgresql_password
}


///////////////////////////////////////
// Populate Vault with Flexible DB info
//////////////////////////////////////

resource "azurerm_key_vault_secret" "POSTGRES-USER-FLEXIBLE" {
  key_vault_id = data.azurerm_key_vault.key_vault.id
  name         = "${var.component}-POSTGRES-USER-FLEXIBLE"
  value        = module.lau-idam-db-flexible.username
}

resource "azurerm_key_vault_secret" "POSTGRES-PASS-FLEXIBLE" {
  key_vault_id = data.azurerm_key_vault.key_vault.id
  name         = "${var.component}-POSTGRES-PASS-FLEXIBLE"
  value        = module.lau-idam-db-flexible.password
}

resource "azurerm_key_vault_secret" "POSTGRES_HOST_FLEXIBLE" {
  key_vault_id = data.azurerm_key_vault.key_vault.id
  name         = "${var.component}-POSTGRES-HOST-FLEXIBLE"
  value        = module.lau-idam-db-flexible.fqdn
}

resource "azurerm_key_vault_secret" "POSTGRES_PORT_FLEXIBLE" {
  key_vault_id = data.azurerm_key_vault.key_vault.id
  name      = "${var.component}-POSTGRES-PORT-FLEXIBLE"
  value     =  var.postgresql_flexible_server_port
}
