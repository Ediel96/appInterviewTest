module "platform" {
  source = "../../modules/environment"

  project_name               = var.project_name
  aws_region                 = var.aws_region
  environment                = "prod"
  vpc_cidr                   = var.vpc_cidr
  single_nat_gateway         = false
  image_tag                  = var.image_tag
  ecr_force_delete           = false
  task_cpu                   = 512
  task_memory                = 1024
  desired_count              = 2
  min_capacity               = 2
  max_capacity               = 4
  use_fargate_spot           = false
  certificate_arn            = var.certificate_arn
  openapi_server_url         = var.openapi_server_url
  log_retention_days         = 90
  enable_deletion_protection = true
  tags                       = var.tags
}
