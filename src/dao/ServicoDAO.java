package dao;
import model.Servico;

public class ServicoDAO {
    private Arquivo<Servico> arqServicos;

    public ServicoDAO() throws Exception {
        arqServicos = new Arquivo<>("servicos", Servico.class.getConstructor());
    }

    public boolean incluirServico(Servico servico) throws Exception {
        // Validar se nome já existe
        if (buscarServicoPorNome(servico.getNome()) != null) {
            throw new IllegalArgumentException("Já existe um serviço cadastrado com o nome: " + servico.getNome());
        }
        
        return arqServicos.create(servico) > 0;
    }

    public boolean alterarServico(Servico servico) throws Exception {
        // Buscar serviço existente
        Servico servicoExistente = arqServicos.read(servico.getId());
        if (servicoExistente == null) {
            throw new IllegalArgumentException("Serviço não encontrado com ID: " + servico.getId());
        }
        
        // Validar se nome mudou e se já existe outro serviço com o novo nome
        if (!servicoExistente.getNome().equals(servico.getNome())) {
            Servico servicoComMesmoNome = buscarServicoPorNome(servico.getNome());
            if (servicoComMesmoNome != null && servicoComMesmoNome.getId() != servico.getId()) {
                throw new IllegalArgumentException("Já existe outro serviço cadastrado com o nome: " + servico.getNome());
            }
        }
        
        return arqServicos.update(servico);
    }

    public boolean excluirServico(int id) throws Exception {
        // Excluir em cascata: primeiro excluir todos os agendamentos deste serviço
        AgendarDAO agendarDAO = new AgendarDAO();
        agendarDAO.excluirAgendamentosPorServico(id);
        
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
