package id.go.kemenkoinfra.ipfo.sifpi.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import id.go.kemenkoinfra.ipfo.sifpi.auth.model.Resource;

public interface ResourceRepository extends JpaRepository<Resource, String> {
}
