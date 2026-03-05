package id.go.kemenkoinfra.ipfo.sifpi.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import id.go.kemenkoinfra.ipfo.sifpi.project.model.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
