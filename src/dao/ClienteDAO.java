package dao;
import model.Cliente;

public class ClienteDAO {
    private Arquivo<Cliente> arqClientes;
    private IndiceHashExtensivel indiceHash;

    public ClienteDAO() throws Exception {
        arqClientes = new Arquivo<>("clientes", Cliente.class.getConstructor());
        // Usar o mesmo índice hash que o PetDAO para manter consistência
        indiceHash = new IndiceHashExtensivel("pets");
    }

    public boolean incluirCliente(Cliente cliente) throws Exception {
        // Validar se CPF já existe
        if (buscarClientePorCPF(cliente.getCpf()) != null) {
            throw new IllegalArgumentException("Já existe um cliente cadastrado com o CPF: " + cliente.getCpf());
        }
        
        // Validar se email já existe (se fornecido)
        if (cliente.getEmail() != null && !cliente.getEmail().trim().isEmpty()) {
            if (buscarClientePorEmail(cliente.getEmail()) != null) {
                throw new IllegalArgumentException("Já existe um cliente cadastrado com o email: " + cliente.getEmail());
            }
        }
        
        return arqClientes.create(cliente) > 0;
    }

    public boolean alterarCliente(Cliente cliente) throws Exception {
        // Buscar cliente existente
        Cliente clienteExistente = arqClientes.read(cliente.getId());
        if (clienteExistente == null) {
            throw new IllegalArgumentException("Cliente não encontrado com ID: " + cliente.getId());
        }
        
        // Validar se CPF mudou e se já existe outro cliente com o novo CPF
        if (!clienteExistente.getCpf().equals(cliente.getCpf())) {
            Cliente clienteComMesmoCPF = buscarClientePorCPF(cliente.getCpf());
            if (clienteComMesmoCPF != null && clienteComMesmoCPF.getId() != cliente.getId()) {
                throw new IllegalArgumentException("Já existe outro cliente cadastrado com o CPF: " + cliente.getCpf());
            }
        }
        
        // Validar se email mudou e se já existe outro cliente com o novo email
        if (cliente.getEmail() != null && !cliente.getEmail().trim().isEmpty()) {
            if (!cliente.getEmail().equals(clienteExistente.getEmail())) {
                Cliente clienteComMesmoEmail = buscarClientePorEmail(cliente.getEmail());
                if (clienteComMesmoEmail != null && clienteComMesmoEmail.getId() != cliente.getId()) {
                    throw new IllegalArgumentException("Já existe outro cliente cadastrado com o email: " + cliente.getEmail());
                }
            }
        }
        
        return arqClientes.update(cliente);
    }

    public boolean excluirCliente(int id) throws Exception {
        // Buscar o cliente para obter o CPF antes de excluir
        Cliente cliente = arqClientes.read(id);
        if (cliente == null) {
            return false;
        }
        
        // Excluir em cascata: primeiro excluir todos os pets do cliente
        PetDAO petDAO = new PetDAO();
        java.util.List<Integer> idsPets = indiceHash.buscarIdsPetsPorCpf(cliente.getCpf());
        
        // Excluir cada pet
        for (Integer idPet : idsPets) {
            petDAO.excluirPet(idPet);
        }
        
        // Excluir o cliente do arquivo
        boolean excluido = arqClientes.delete(id);
        
        if (excluido) {
            // Remover todos os relacionamentos Pet-Dono da hash extensível
            indiceHash.removerTodosPorCpf(cliente.getCpf());
        }
        
        return excluido;
    }

    public Cliente buscarCliente(int id) throws Exception {
        return arqClientes.read(id);
    }

    public Cliente buscarClientePorCPF(String cpf) throws Exception {
        return arqClientes.findBy(cliente -> cliente.getCpf().equals(cpf));
    }

    public boolean alterarClientePorCPF(String cpf, Cliente novoCliente) throws Exception {
        // Buscar cliente pelo CPF
        Cliente clienteExistente = buscarClientePorCPF(cpf);
        if (clienteExistente == null) {
            return false; // Cliente não encontrado
        }
        
        // Manter o ID original e definir o novo CPF
        novoCliente.setId(clienteExistente.getId());
        novoCliente.setCpf(cpf);
        
        // Usar o método de alteração por ID
        return arqClientes.update(novoCliente);
    }

    public boolean excluirClientePorCPF(String cpf) throws Exception {
        // Buscar cliente pelo CPF para obter o ID
        Cliente cliente = buscarClientePorCPF(cpf);
        if (cliente == null) {
            return false; // Cliente não encontrado
        }
        
        // Excluir em cascata: primeiro excluir todos os pets do cliente
        PetDAO petDAO = new PetDAO();
        java.util.List<Integer> idsPets = indiceHash.buscarIdsPetsPorCpf(cpf);
        
        // Excluir cada pet
        for (Integer idPet : idsPets) {
            petDAO.excluirPet(idPet);
        }
        
        // Remover todos os relacionamentos Pet-Dono da hash extensível
        indiceHash.removerTodosPorCpf(cpf);
        
        // Usar o método de exclusão por ID
        return arqClientes.delete(cliente.getId());
    }

    public java.util.List<Cliente> buscarClientesPorNome(String nome) throws Exception {
        return arqClientes.findAll(cliente -> cliente.getNome().toLowerCase().contains(nome.toLowerCase()));
    }

    public Cliente buscarClientePorEmail(String email) throws Exception {
        return arqClientes.findBy(cliente -> cliente.getEmail().toLowerCase().equals(email.toLowerCase()));
    }
    
    /**
     * Lista todos os clientes cadastrados (para busca por padrão)
     */
    public java.util.List<Cliente> listarTodos() throws Exception {
        return arqClientes.findAll(cliente -> true);
    }
}
