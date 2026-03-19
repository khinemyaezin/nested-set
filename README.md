# Tree Structure Implementation

This project demonstrates how to implement a tree structure using a composite pattern in Java, extending an abstract `NodeComponent` class. Below are the steps to set up the entities, repositories, service layer, and configuration required to manage a category hierarchy.

This project constructs with,
1. **NodeComponent:** `abstract class`
    - is to serve as the base class for implementing a composite pattern, which is a structural design pattern that allows you to treat individual objects (leaves) and compositions of objects (composites) uniformly.
2. **NestedSetNodeRepository:** `interface`
    - is a repository implementation that provides operations for entities.
3. **TreeBuilder:** `class`
   - is designed to provide a structured way to construct and manage tree-like hierarchical structures.

   
## 1. Create the Category Entity

First, create a `TestNode` entity that extends the `NodeComponent` abstract class. The following annotations are required: `@Id`, `@NameColumn`, `@LeftColumn`, `@RightColumn`, and `@DepthColumn`.

```java
@Entity
@Table(name = "node")
public class TestNode extends NodeComponent {
    @Id
    private Long id;
    @NameColumn
    private String name;
    @LeftColumn
    private Integer lft;
    @RightColumn
    private Integer rgt;
    @DepthColumn
    private Integer depth;
}
```

## 2. Create Repositories
Next, create the necessary repository classes for handling data operations.

```java
    @Bean
    public JpaNestedSetRepositoryConfiguration<CategoryEntity,Long> categoryRepositoryConfiguration(JpaContext context) {
       return new JpaNestedSetRepositoryConfiguration<>(context, CategoryEntity.class);
    }
    
    @Bean
    public NestedSetNodeRepository<CategoryEntity,Long> categoryNodeRepository(JpaNestedSetRepositoryConfiguration<CategoryEntity,Long> configuration) {
       return JpaNestedSetNodeRepositoryFactory.create(configuration, new TreeBuilderImpl<>(new CategoryComponentFactory()));
    }
```

## 3. Create `CategoryLeaf` and `CategoryComposite` Classes
Create the `CategoryLeaf` and `CategoryComposite` classes, both extending `NodeComponent`. These classes represent the leaf nodes and composite nodes of the category tree, respectively.

```java
public class CategoryLeaf<T> extends NodeComponent<T> {
   private T node;
   private NodeComponent<T> parent;

   public CategoryLeaf(T node) {
      super(node);
   }

    // getter, setter
}
public class CategoryComposite<T> extends NodeComponent<T> {
    private T node;
    private NodeComponent<T> parent;
    private final Set<NodeComponent<T>> children = new HashSet<>();

    public CategoryComposite(T node) {
        super(node);
    }
   // getter, setter
}
```

## 4. Create NodeComponentFactory to Support Leaf and Composite Classes

Implement a factory class to create instances of CategoryLeaf and CategoryComposite.

```java
public class CategoryComponentFactory implements NodeComponentFactory<Entity,Long> {

   @Override
   public NodeComponent<Entity> createCompositeNodeComponent(Entity entity) {
      return new CategoryComposite<>(entity);
   }

   @Override
   public NodeComponent<Entity> createLeafNodeComponent(Entity entity) {
      return new CategoryLeaf<>(entity);
   }
}
```
# Conclusion
By following these steps, you can create a robust category tree structure using the composite pattern in Java. The CategoryService class encapsulates the business logic, making it easier to manage and manipulate hierarchical data.
