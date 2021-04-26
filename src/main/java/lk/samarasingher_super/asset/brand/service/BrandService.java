package lk.samarasingher_super.asset.brand.service;


import lk.samarasingher_super.asset.brand.dao.BrandDao;
import lk.samarasingher_super.asset.brand.entity.Brand;
import lk.samarasingher_super.util.interfaces.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "brand")
public class BrandService implements AbstractService<Brand, Integer> {
    private final BrandDao brandDao;

    @Autowired
    public BrandService(BrandDao brandDao) {
        this.brandDao = brandDao;
    }

    @Override
    public List<Brand> findAll() {
        return brandDao.findAll();
    }

    @Override
    public Brand findById(Integer id) {
        return brandDao.getOne(id);
    }

    @Override
    public Brand persist(Brand brand) {
        return brandDao.save(brand);
    }

    @Override
    public boolean delete(Integer id) {
        brandDao.deleteById(id);
        //not applicable
        return false;
    }

    @Override
    public List<Brand> search(Brand brand) {
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Brand> brandExample = Example.of(brand, matcher);
        return brandDao.findAll(brandExample);
    }

    public Brand findByName(String name) {
        return brandDao.findByName(name);

    }

//Brand Service
 public Brand findByNameAndCategoryId(String name,Integer categoryId){
        return brandDao.findByNameAndCategoryId(name,categoryId);
 }
}
