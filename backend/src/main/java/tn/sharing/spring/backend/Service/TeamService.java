package tn.sharing.spring.backend.Service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.sharing.spring.backend.Entity.Team;
import tn.sharing.spring.backend.Repository.TeamRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor

public class TeamService {

    @Autowired
    private TeamRepo teamRepo;

    public Team createTeam(Team team) {
        return teamRepo.save(team);
    }

    public long getTotalTeams() {
        return teamRepo.count();
    }
}