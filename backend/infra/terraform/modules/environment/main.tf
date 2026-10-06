locals {
  name = "${var.project_name}-${var.environment}"
  tags = merge(var.tags, {
    Project     = var.project_name
    Environment = var.environment
    ManagedBy   = "Terraform"
  })
}

module "network" {
  source = "../network"

  name               = local.name
  vpc_cidr           = var.vpc_cidr
  single_nat_gateway = var.single_nat_gateway
  tags               = local.tags
}

module "service" {
  source = "../service"

  name                       = local.name
  aws_region                 = var.aws_region
  environment                = var.environment
  vpc_id                     = module.network.vpc_id
  vpc_cidr                   = module.network.vpc_cidr
  public_subnet_ids          = module.network.public_subnet_ids
  private_subnet_ids         = module.network.private_subnet_ids
  image_tag                  = var.image_tag
  ecr_force_delete           = var.ecr_force_delete
  task_cpu                   = var.task_cpu
  task_memory                = var.task_memory
  desired_count              = var.desired_count
  min_capacity               = var.min_capacity
  max_capacity               = var.max_capacity
  use_fargate_spot           = var.use_fargate_spot
  dummyjson_api_base_url     = var.dummyjson_api_base_url
  dummyjson_api_timeout_ms   = var.dummyjson_api_timeout_ms
  openapi_server_url         = var.openapi_server_url
  certificate_arn            = var.certificate_arn
  log_retention_days         = var.log_retention_days
  enable_deletion_protection = var.enable_deletion_protection
  tags                       = local.tags
}
