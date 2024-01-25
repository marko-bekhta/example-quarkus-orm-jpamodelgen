package org.acme;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.quarkus.narayana.jta.QuarkusTransaction;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.model.MyEntity;
import org.acme.model.MyEntity_;

@Path("/hello")
public class GreetingResource {

	@Inject
	EntityManager entityManager;

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String hello() {
		Set<String> errors = new HashSet<>();

		QuarkusTransaction.requiringNew().run( () -> {
			entityManager.persist( new MyEntity( 1L, "text 1", 10, LocalDateTime.of( 2024, 1, 1, 12, 0 ) ) );
			entityManager.persist( new MyEntity( 2L, "text 2", 20, LocalDateTime.of( 2024, 1, 2, 12, 0 ) ) );
			entityManager.persist( new MyEntity( 3L, "text 3", 30, LocalDateTime.of( 2024, 1, 3, 12, 0 ) ) );
			entityManager.persist( new MyEntity( 4L, "text 4", 40, LocalDateTime.of( 2024, 1, 4, 12, 0 ) ) );
		} );

		QuarkusTransaction.requiringNew().run( () -> {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<MyEntity> criteriaQuery = criteriaBuilder.createQuery( MyEntity.class );

			Root<MyEntity> root = criteriaQuery.from( MyEntity.class );
			criteriaQuery.select( root ).where( criteriaBuilder.ge( root.get( MyEntity_.integer ), 25 ) );

			TypedQuery<MyEntity> query = entityManager.createQuery( criteriaQuery );
			List<MyEntity> results = query.getResultList();
			if (results.size() != 2 ) {
				errors.add( "selecting by `integer > 25` failed, and returned incorrect results." );
			}
		} );

		QuarkusTransaction.requiringNew().run( () -> {
			entityManager.createQuery( "delete from MyEntity" );
		} );

		return "Hello RESTEasy" + ( errors.isEmpty() ? "" : ( ". With errors: " + errors ) );
	}
}
