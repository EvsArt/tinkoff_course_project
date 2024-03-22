package edu.java.domain.jpaRepository;

import edu.java.model.entity.Link;
import edu.java.model.entity.StackOverFlowLinkInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.net.URI;
import java.util.Optional;

@Repository
public interface JpaStackOverFlowLinkInfoRepository extends JpaRepository<StackOverFlowLinkInfo, Long> {

    Optional<StackOverFlowLinkInfo> findByLinkId(long linkId);

    Optional<StackOverFlowLinkInfo> findByLinkUrl(URI url);

    int deleteByLink(Link link);

}