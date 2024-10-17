package com.nestedset.app.repository;

import com.nestedset.library.model.NodeComponent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaNodeRepository<T extends NodeComponent,ID> extends JpaRepository<T,ID>,NodeRepository<T,ID> {
    List<T> findAllByOrderByLft();
}
