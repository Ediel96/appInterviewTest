output "application_url" {
  value       = module.platform.application_url
  description = "URL pública del backend de staging."
}

output "ecr_repository_url" {
  value       = module.platform.ecr_repository_url
  description = "ECR de staging."
}

output "ecs_cluster_name" {
  value       = module.platform.ecs_cluster_name
  description = "Cluster ECS de staging."
}

output "ecs_service_name" {
  value       = module.platform.ecs_service_name
  description = "Servicio ECS de staging."
}

output "cloudwatch_log_group" {
  value       = module.platform.cloudwatch_log_group
  description = "Logs del backend de staging."
}

