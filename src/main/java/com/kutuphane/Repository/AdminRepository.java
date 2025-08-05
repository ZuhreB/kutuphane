package com.kutuphane.Repository;

import com.kutuphane.Entity.Publisher;
import org.springframework.data.repository.CrudRepository;

public interface AdminRepository extends CrudRepository<Publisher,Integer> {
}
