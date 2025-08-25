package com.gabriel.danael_fernandes_react_manager.core

class ResourceNotFoundException(
    resourceName: String,
    id: Any
) : RuntimeException("Recurso '$resourceName' com id '$id' não encontrado.")

class ResourceConflictException(
    message: String
) : RuntimeException(message)

class BadRequestException(
    message: String
) : RuntimeException(message)

class AuthenticationException(
    message: String = "Autenticação necessária."
) : RuntimeException(message)

class AuthorizationException(
    message: String = "Você não tem permissão para realizar esta ação."
) : RuntimeException(message)

class BusinessRuleException(
    message: String
) : RuntimeException(message)

class IntegrationException(
    serviceName: String,
    cause: Throwable?
) : RuntimeException("Falha na comunicação com o serviço externo: $serviceName", cause)