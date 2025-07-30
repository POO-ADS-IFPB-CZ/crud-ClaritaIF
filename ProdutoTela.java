import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProdutoTela implements GenericDao<Produto, String> {
   private Map<String, Produto> produtos = new HashMap<>();

    @Override
    public void save(Produto produto) {
        produtos.put(produto.getCodigo(), produto);
        System.out.println("Produto salvo/atualizado: " + produto.getCodigo());
    }

    @Override
    public Produto findById(String codigo) {
        return produtos.get(codigo);
    }

    @Override
    public List<Produto> findAll() {
        return new ArrayList<>(produtos.values());
    }

    @Override
    public void update(Produto produto) {
        if (produtos.containsKey(produto.getCodigo())) {
            produtos.put(produto.getCodigo(), produto);
            System.out.println("Produto atualizado: " + produto.getCodigo());
        } else {
            System.out.println("Produto com codigo " + produto.getCodigo() + " nao encontrado para atualizacao.");
        }
    }

    @Override
    public void delete(String codigo) {
        Produto removed = produtos.remove(codigo);
        if (removed != null) {
            System.out.println("Produto removido: " + codigo);
        } else {
            System.out.println("Produto com codigo " + codigo + " nao encontrado para remocao.");
        }
    }
}
