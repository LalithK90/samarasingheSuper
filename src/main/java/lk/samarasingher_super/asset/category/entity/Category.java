package lk.samarasingher_super.asset.category.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import lk.samarasingher_super.asset.brand.entity.Brand;
import lk.samarasingher_super.asset.common_asset.model.enums.LiveDead;
import lk.samarasingher_super.asset.item.entity.Item;
import lk.samarasingher_super.asset.item.entity.enums.MainCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonFilter( "Category" )
public class Category {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Integer id;

    @Enumerated(EnumType.STRING)
    private MainCategory mainCategory;

    @Enumerated(EnumType.STRING)
    private LiveDead liveDead;

    @Column( unique = true )
    @Size( min = 4, message = "Your Subcategory Name cannot be accepted" )
    private String name;

    @OneToMany(mappedBy = "category")
    private List< Brand > brands;

    @OneToMany(mappedBy = "category",cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Item> items;



}
