package lk.SamarasingheSuper.asset.good_received_note.dao;

import lk.SamarasingheSuper.asset.good_received_note.entity.GoodReceivedNote;
import lk.SamarasingheSuper.asset.purchase_order.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodReceivedNoteDao extends JpaRepository<GoodReceivedNote, Integer> {
  GoodReceivedNote findByPurchaseOrder(PurchaseOrder purchaseOrder);
}
