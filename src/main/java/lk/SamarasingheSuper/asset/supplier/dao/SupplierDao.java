package lk.SamarasingheSuper.asset.supplier.dao;

import lk.SamarasingheSuper.asset.supplier.entity.Supplier;
import lk.SamarasingheSuper.asset.supplier_item.entity.enums.ItemSupplierStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface SupplierDao extends JpaRepository<Supplier, Integer> {
    Supplier findFirstByOrderByIdDesc();

    Supplier findByIdAndItemSupplierStatus(Integer supplierId, ItemSupplierStatus itemSupplierStatus);
}
