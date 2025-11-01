package dao;

import model.Agendar;
import java.util.ArrayList;
import java.util.List;

public class AgendarDAO {
    private Arquivo<Agendar> arqAgendamentos;
    private ArvoreBMais<RegistroAgendamento> indiceBMais;

    public AgendarDAO() throws Exception {
        arqAgendamentos = new Arquivo<>("agendamentos", Agendar.class.getConstructor());
        indiceBMais = new ArvoreBMais<>(
            RegistroAgendamento.class.getConstructor(),
            5,  // Ordem da árvore
            "src/dados/agendamentos/agendamentos_bmais.db"
        );
    }

    /**
     * Inclui um novo agendamento
     * Valida se já existe um agendamento para o mesmo pet e serviço
     */
    public boolean incluirAgendamento(Agendar agendamento) throws Exception {
        // Validar se já existe agendamento para este pet com este serviço
        if (existeAgendamento(agendamento.getIdPet(), agendamento.getIdServico())) {
            throw new IllegalArgumentException(
                "Já existe um agendamento para este pet com este serviço"
            );
        }
        
        // Criar o agendamento no arquivo principal
        int idGerado = arqAgendamentos.create(agendamento);
        
        if (idGerado > 0) {
            // Inserir na Árvore B+ o relacionamento (idPet, idServico) -> idAgendamento
            RegistroAgendamento registro = new RegistroAgendamento(
                agendamento.getIdPet(),
                agendamento.getIdServico(),
                idGerado
            );
            indiceBMais.create(registro);
            return true;
        }
        
        return false;
    }

    /**
     * Altera um agendamento existente
     */
    public boolean alterarAgendamento(Agendar agendamento) throws Exception {
        // Buscar agendamento existente
        Agendar agendamentoExistente = arqAgendamentos.read(agendamento.getId());
        if (agendamentoExistente == null) {
            throw new IllegalArgumentException("Agendamento não encontrado com ID: " + agendamento.getId());
        }
        
        // Se mudou o pet ou serviço, validar se não existe outro agendamento
        if (agendamentoExistente.getIdPet() != agendamento.getIdPet() ||
            agendamentoExistente.getIdServico() != agendamento.getIdServico()) {
            
            if (existeAgendamento(agendamento.getIdPet(), agendamento.getIdServico())) {
                throw new IllegalArgumentException(
                    "Já existe outro agendamento para este pet com este serviço"
                );
            }
            
            // Remover o registro antigo da Árvore B+
            RegistroAgendamento registroAntigo = new RegistroAgendamento(
                agendamentoExistente.getIdPet(),
                agendamentoExistente.getIdServico(),
                agendamento.getId()
            );
            indiceBMais.delete(registroAntigo);
            
            // Inserir o novo registro na Árvore B+
            RegistroAgendamento registroNovo = new RegistroAgendamento(
                agendamento.getIdPet(),
                agendamento.getIdServico(),
                agendamento.getId()
            );
            indiceBMais.create(registroNovo);
        }
        
        return arqAgendamentos.update(agendamento);
    }

    /**
     * Exclui um agendamento
     */
    public boolean excluirAgendamento(int idPet, int idServico) throws Exception {
        // Buscar o ID do agendamento na Árvore B+
        RegistroAgendamento chave = new RegistroAgendamento(idPet, idServico, 0);
        ArrayList<RegistroAgendamento> resultados = indiceBMais.read(chave);
        
        if (resultados == null || resultados.isEmpty()) {
            return false;
        }
        
        // Pegar o primeiro resultado (deveria haver apenas um)
        RegistroAgendamento registro = resultados.get(0);
        int idAgendamento = registro.getIdAgendamento();
        
        // Remover do arquivo principal
        boolean removido = arqAgendamentos.delete(idAgendamento);
        
        if (removido) {
            // Remover da Árvore B+
            indiceBMais.delete(registro);
        }
        
        return removido;
    }

    /**
     * Busca um agendamento específico por pet e serviço
     */
    public Agendar buscarAgendamento(int idPet, int idServico) throws Exception {
        RegistroAgendamento chave = new RegistroAgendamento(idPet, idServico, 0);
        ArrayList<RegistroAgendamento> resultados = indiceBMais.read(chave);
        
        if (resultados == null || resultados.isEmpty()) {
            return null;
        }
        
        int idAgendamento = resultados.get(0).getIdAgendamento();
        return arqAgendamentos.read(idAgendamento);
    }

    /**
     * Busca todos os agendamentos de um pet específico
     */
    public List<Agendar> buscarAgendamentosPorPet(int idPet) throws Exception {
        List<Agendar> agendamentos = new ArrayList<>();
        
        // Como a árvore B+ não suporta busca por chave parcial diretamente,
        // vamos usar o arquivo principal e filtrar
        List<Agendar> todos = arqAgendamentos.findAll(a -> a.getIdPet() == idPet);
        
        return todos;
    }

    /**
     * Busca todos os agendamentos de um serviço específico
     */
    public List<Agendar> buscarAgendamentosPorServico(int idServico) throws Exception {
        List<Agendar> agendamentos = new ArrayList<>();
        
        // Usar o arquivo principal e filtrar
        List<Agendar> todos = arqAgendamentos.findAll(a -> a.getIdServico() == idServico);
        
        return todos;
    }

    /**
     * Lista todos os agendamentos
     */
    public List<Agendar> listarTodosAgendamentos() throws Exception {
        // Buscar todos os agendamentos do arquivo principal
        return arqAgendamentos.findAll(a -> true);
    }

    /**
     * Verifica se existe um agendamento para um pet com um serviço
     */
    private boolean existeAgendamento(int idPet, int idServico) throws Exception {
        RegistroAgendamento chave = new RegistroAgendamento(idPet, idServico, 0);
        ArrayList<RegistroAgendamento> resultados = indiceBMais.read(chave);
        return resultados != null && !resultados.isEmpty();
    }

    /**
     * Exclui todos os agendamentos de um pet específico (exclusão em cascata)
     */
    public int excluirAgendamentosPorPet(int idPet) throws Exception {
        List<Agendar> agendamentos = buscarAgendamentosPorPet(idPet);
        int count = 0;
        
        for (Agendar agendamento : agendamentos) {
            if (excluirAgendamento(agendamento.getIdPet(), agendamento.getIdServico())) {
                count++;
            }
        }
        
        return count;
    }

    /**
     * Exclui todos os agendamentos de um serviço específico (exclusão em cascata)
     */
    public int excluirAgendamentosPorServico(int idServico) throws Exception {
        List<Agendar> agendamentos = buscarAgendamentosPorServico(idServico);
        int count = 0;
        
        for (Agendar agendamento : agendamentos) {
            if (excluirAgendamento(agendamento.getIdPet(), agendamento.getIdServico())) {
                count++;
            }
        }
        
        return count;
    }

    public void close() throws Exception {
        // Fechar recursos se necessário
    }
}

