package dao;
import model.Servico;

public class ServicoDAO {
    private Arquivo<Servico> arqServicos;

    public ServicoDAO() throws Exception {
        arqServicos = new Arquivo<>("servicos", Servico.class.getConstructor());
    }

    public boolean incluirServico(Servico servico) throws Exception {
        return arqServicos.create(servico) > 0;
    }

    public boolean alterarServico(Servico servico) throws Exception {
        return arqServicos.update(servico);
    }

    public boolean excluirServico(int id) throws Exception {
        return arqServicos.delete(id);
    }

    public Servico buscarServico(int id) throws Exception {
        return arqServicos.read(id);
    }

    public Servico buscarServicoPorNome(String nome) throws Exception {
        return arqServicos.findBy(servico -> servico.getNome().toLowerCase().equals(nome.toLowerCase()));
    }

    public java.util.List<Servico> buscarServicosPorFaixaPreco(int valorMin, int valorMax) throws Exception {
        // Busca TODOS os serviços na faixa de preço usando findAll
        return arqServicos.findAll(s -> s.getValor() >= valorMin && s.getValor() <= valorMax);
    }

    public void close() throws Exception {
        // Fechar o arquivo se necessário
    }
}
