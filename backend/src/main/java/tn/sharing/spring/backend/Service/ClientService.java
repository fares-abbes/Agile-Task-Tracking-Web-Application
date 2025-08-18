package tn.sharing.spring.backend.Service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.sharing.spring.backend.Entity.Client;
import tn.sharing.spring.backend.Repository.ClientRepo;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ClientService {

    @Autowired
    private ClientRepo clientRepo;

    public Client createClient(Client client) {
        return clientRepo.save(client);
    }

    public Optional<Client> getClientById(int clientId) {
        return clientRepo.findById(clientId);
    }

    public List<Client> getAllClients() {
        return clientRepo.findAll();
    }

    public Client updateClient(int clientId, Client clientDetails) {
        return clientRepo.findById(clientId).map(client -> {
            client.setClientName(clientDetails.getClientName());
            return clientRepo.save(client);
        }).orElse(null);
    }

    public boolean deleteClient(int clientId) {
        if (clientRepo.existsById(clientId)) {
            clientRepo.deleteById(clientId);
            return true;
        }
        return false;
    }

    public long getTotalClients() {
        return clientRepo.count();
    }
}
