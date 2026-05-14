package com.rms.repository;
import com.rms.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByOrderIdOrderByCreatedAtAsc(Long orderId);
    long countByOrderIdAndIsReadFalseAndSender_RoleNot(Long orderId, com.rms.entity.User.Role role);
}
