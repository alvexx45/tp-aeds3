package dao;
import model.Cliente;

public class ClienteDAO {
    private Arquivo<Cliente> arqClientes;

    public ClienteDAO() throws Exception {
        arqClientes = new Arquivo<>("clientes", Cliente.class.getConstructor());
    }

    public boolean incluirCliente(Cliente cliente) throws Exception {
        return arqClientes.create(cliente) > 0;
    }

    public boolean alterarCliente(Cliente cliente) throws Exception {
        return arqClientes.update(cliente);
    }

    public boolean excluirCliente(int id) throws Exception {
        return arqClientes.delete(id);
    }

    public Cliente buscarCliente(int id) throws Exception {
        return arqClientes.read(id);
    }

    public Cliente buscarClientePorCPF(String cpf) throws Exception {
        return arqClientes.findBy(cliente -> cliente.getCpf().equals(cpf));
    }
}
