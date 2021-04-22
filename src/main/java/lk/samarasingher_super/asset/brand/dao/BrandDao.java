package lk.samarasingher_super.asset.brand.dao;


import lk.samarasingher_super.asset.brand.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandDao extends JpaRepository<Brand, Integer> {
  Brand findByName(String nic);

//Brand Dao
  Brand findByNameAndCategoryId(String name,Integer categoryId);
}
