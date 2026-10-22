
package tz.tante.rent.manager.exceptions.graphql;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import tz.tante.rent.manager.exceptions.ResourceNotFoundException;

@ControllerAdvice
@Slf4j
public class GlobalGraphQlExceptionAdvice {

  @GraphQlExceptionHandler
  public GraphQLError handleIllegalArgument(IllegalArgumentException ex)
  {
    return GraphqlErrorBuilder.newError()
      .errorType(ErrorType.BAD_REQUEST)
      .message(ex.getMessage())
      .build();
  }

  @GraphQlExceptionHandler
  public GraphQLError handleUnexpectedException(Exception ex)
  {

    log.error("Unexpected error while processing GraphQL request", ex);

    return GraphqlErrorBuilder.newError()
      .errorType(ErrorType.INTERNAL_ERROR)
      .message(ex.getMessage())
      .build();
  }


  @GraphQlExceptionHandler
  public GraphQLError handleTenantNotFound(ResourceNotFoundException ex) {

    return GraphqlErrorBuilder.newError()
      .errorType(ErrorType.NOT_FOUND)
      .message(ex.getMessage())
      .build();
  }

  @GraphQlExceptionHandler
  public GraphQLError handleConstraintViolation(ConstraintViolationException ex)
  {

    String message = ex.getConstraintViolations()
      .stream()
      .map(violation -> violation.getMessage())
      .distinct()
      .reduce((first, second) -> first + "; " + second)
      .orElse("Invalid request input");

    return GraphqlErrorBuilder.newError()
      .errorType(ErrorType.BAD_REQUEST)
      .message(message)
      .build();
  }

}
