import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class App extends JFrame {
    private JTextField codigoField;
    private JTextField descricaoField;
    private JTextField precoField;

    private JButton adicionarButton;
    private JButton atualizarButton;
    private JButton removerButton;
    
    private JTable produtoTable;
    private DefaultTableModel tableModel; 
    private ProdutoTela produtoDao;

    public App() {
        super("Gerenciamento de Produtos"); 
        produtoDao = new ProdutoTela(); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setSize(700, 500); 
        setLocationRelativeTo(null);

        codigoField = new JTextField(15);
        descricaoField = new JTextField(25);
        precoField = new JTextField(10);

        adicionarButton = new JButton("Adicionar");
        atualizarButton = new JButton("Atualizar");
        removerButton = new JButton("Remover");

        String[] columnNames = {"Codigo", "Descricao", "Preco"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; }
        };
        produtoTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(produtoTable); 
        setLayout(new BorderLayout(10, 10)); 
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); 
        gbc.fill = GridBagConstraints.HORIZONTAL; 

        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Codigo:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; inputPanel.add(codigoField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("Descricao:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; inputPanel.add(descricaoField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("Preco:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; inputPanel.add(precoField, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(adicionarButton);
        buttonPanel.add(atualizarButton);
        buttonPanel.add(removerButton);

        add(inputPanel, BorderLayout.NORTH); 
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        adicionarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adicionarProduto();
            }
        });

        atualizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                atualizarProduto();
            }
        });

        removerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removerProduto();
            }
        });
    
        produtoTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = produtoTable.getSelectedRow();
                if (selectedRow != -1) { 
                    codigoField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    descricaoField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    precoField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                }
            }
        });
        loadProdutos();
    }
    private void loadProdutos() {
        tableModel.setRowCount(0); 
        List<Produto> produtos = produtoDao.findAll(); 
        for (Produto p : produtos) {
            tableModel.addRow(new Object[]{p.getCodigo(), p.getDescricao(), p.getPreco()});
        }
    }

    private void adicionarProduto() {
        try {
            String codigo = codigoField.getText().trim();
            String descricao = descricaoField.getText().trim();
            double preco = Double.parseDouble(precoField.getText().trim());

            if (codigo.isEmpty() || descricao.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Codigo e Descricao nao podem ser vazios.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (produtoDao.findById(codigo) != null) {
                JOptionPane.showMessageDialog(this, "Produto com este codigo ja existe.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Produto novoProduto = new Produto(codigo, descricao, preco);
            produtoDao.save(novoProduto);
            loadProdutos(); 
            clearFields(); 
            JOptionPane.showMessageDialog(this, "Produto adicionado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Preco invalido. Por favor, insira um numero valido.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void atualizarProduto() {
        try {
            String codigo = codigoField.getText().trim();
            String descricao = descricaoField.getText().trim();
            double preco = Double.parseDouble(precoField.getText().trim());

            if (codigo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecione um produto na tabela ou insira um codigo para atualizar.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Produto produtoExistente = produtoDao.findById(codigo);
            if (produtoExistente == null) {
                JOptionPane.showMessageDialog(this, "Produto com codigo " + codigo + " nao encontrado para atualizacao.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            produtoExistente.setDescricao(descricao);
            produtoExistente.setPreco(preco);
            produtoDao.update(produtoExistente);
            loadProdutos();
            clearFields();
            JOptionPane.showMessageDialog(this, "Produto atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Preco invalido. Por favor, insira um numero valido.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void removerProduto() {
        String codigo = codigoField.getText().trim();
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela ou insira um codigo para remover.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja remover o produto " + codigo + "?", "Confirmar Remocao", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Produto produtoParaRemover = produtoDao.findById(codigo);
            if (produtoParaRemover != null) {
                produtoDao.delete(codigo); 
                loadProdutos(); 
                clearFields(); 
                JOptionPane.showMessageDialog(this, "Produto removido com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Produto com codigo " + codigo + " nao encontrado para remocao.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void clearFields() {
        codigoField.setText("");
        descricaoField.setText("");
        precoField.setText("");
        produtoTable.clearSelection();
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new App().setVisible(true);
            }
        });
    }
}
