package com.virtu_stock.User.Applied_IPOs;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.virtu_stock.IPO.IPO;
import com.virtu_stock.User.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppliedIpoService {
    private final AppliedIpoRepository appliedIpoRepository;

    public AppliedIpo save(AppliedIpo appliedIpo) {
        return appliedIpoRepository.save(appliedIpo);
    }

    public List<AppliedIpo> findAll() {
        return appliedIpoRepository.findAll();
    }

    public List<AppliedIpo> findByUser(User user) {
        return appliedIpoRepository.findByUser(user);
    }

    public boolean existsByUserAndIpo(User user, IPO ipo) {
        return appliedIpoRepository.existsByUserAndIpo(user, ipo);
    }

    public Optional<AppliedIpo> findByUserAndIpo(User user, IPO ipo) {
        return appliedIpoRepository.findByUserAndIpo(user, ipo);

    }

    public AppliedIpo findById(UUID id) {
        return appliedIpoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Applied ipo not found with id:" + id));
    }

    @Transactional
    public void deleteByUserAndIpo(User user, IPO ipo) {
        appliedIpoRepository.deleteByUserAndIpo(user, ipo);
    }

    public List<Object[]> countAppliedByUserAndMonthAndYear(UUID id, int year) {
        return appliedIpoRepository.countAppliedByUserMonthYear(id, year);
    }

    public List<Object[]> countAllotedByUserMonthAndYear(UUID id, int year) {
        return appliedIpoRepository.countAllotedByUserMonthYear(id, year);
    }
}
