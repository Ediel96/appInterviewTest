output "application_url" {
  description = "URL pública del backend."
  value       = module.service.application_url
}

output "alb_dns_name" {
  description = "DNS del ALB."
  value       = module.service.alb_dns_name
}

output "ecr_repository_url" {
  description = "URL del ECR del ambiente."
  value       = module.service.ecr_repository_url
}

output "ecs_cluster_name" {
  description = "Cluster ECS."
  value       = module.service.ecs_cluster_name
}

output "ecs_service_name" {
  description = "Servicio ECS."
  value       = module.service.ecs_service_name
}

output "cloudwatch_log_group" {
  description = "Grupo de logs del backend."
  value       = module.service.cloudwatch_log_group
}

