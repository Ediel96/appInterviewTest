module "platform" {
  source = "../../modules/environment"

  project_name               = var.project_name
  aws_region                 = var.aws_region
  environment                = "staging"
  vpc_cidr                   = var.vpc_cidr
  single_nat_gateway         = var.single_nat_gateway
  image_tag                  = var.image_tag
  ecr_force_delete           = true
  task_cpu                   = 512
  task_memory                = 1024
  desired_count              = 1
  min_capacity               = 1
  max_capacity               = 2
  use_fargate_spot           = true
  certificate_arn            = var.certificate_arn
  openapi_server_url         = var.openapi_server_url
  log_retention_days         = 14
  enable_deletion_protection = false
  tags                       = var.tags
}
