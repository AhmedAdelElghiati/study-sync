package elghiati.studysync.repository;

import elghiati.studysync.entity.PollVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface PollVoteRepository extends JpaRepository<PollVote, UUID> {

    @Query("""
            SELECT v.pollOption.id AS pollOptionId, COUNT(v.id) AS voteCount 
            FROM PollVote v 
            WHERE v.pollOption.poll.id = :pollId 
            GROUP BY v.pollOption.id
            """)
    List<PollOptionVoteCount> countVotesByPollId(@Param("pollId") UUID pollId);

    @Query("""
            SELECT v.pollOption.id AS pollOptionId, COUNT(v.id) AS voteCount 
            FROM PollVote v 
            WHERE v.pollOption.poll.id IN :pollIds
            GROUP BY v.pollOption.id
            """)
    List<PollOptionVoteCount> countVotesByPollIds(@Param("pollIds") List<UUID> pollIds);

    @Query("""
            SELECT v.pollOption.id 
            FROM PollVote v 
            WHERE v.student.id = :studentId 
            AND v.pollOption.poll.id = :pollId
            """)
    Set<UUID> findVotedOptionIdsByStudentAndPollId(@Param("studentId") UUID studentId, @Param("pollId") UUID pollId);

    @Query("""
            SELECT v.pollOption.id 
            FROM PollVote v 
            WHERE v.student.id = :studentId 
            AND v.pollOption.poll.id IN :pollIds
            """)
    Set<UUID> findVotedOptionIdsByStudentAndPollIds(@Param("studentId") UUID studentId, @Param("pollIds") List<UUID> pollIds);

    interface PollOptionVoteCount {
        UUID getPollOptionId();
        long getVoteCount();
    }
}

