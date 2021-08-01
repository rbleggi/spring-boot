package br.com.croa.api.modules.croa.repository;

import br.com.croa.api.modules.croa.entity.AuditClient;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuditRepository extends MongoRepository<AuditClient, String> {

}