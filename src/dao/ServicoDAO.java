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
}
