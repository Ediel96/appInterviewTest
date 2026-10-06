output "alb_dns_name" {
  description = "DNS público del Application Load Balancer."
  value       = aws_lb.this.dns_name
}

output "application_url" {
  description = "URL base calculada de la aplicación."
  value       = local.openapi_server_url
}

output "ecr_repository_url" {
  description = "Repositorio ECR donde publicar la imagen."
  value       = aws_ecr_repository.this.repository_url
}

output "ecs_cluster_name" {
  description = "Nombre del cluster ECS."
  value       = aws_ecs_cluster.this.name
}

output "ecs_service_name" {
  description = "Nombre del servicio ECS."
  value       = aws_ecs_service.this.name
}

output "cloudwatch_log_group" {
  description = "Log group del contenedor."
  value       = aws_cloudwatch_log_group.this.name
}

