package success.planfit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.domain.post.SpacePost;
import success.planfit.repository.SpacePostRepository;
import success.planfit.repository.UserRepository;

@Transactional
@RequiredArgsConstructor
@Service
public class PostService {

    // 리포지토리를 가져오려고 하는데 그 기준을 잘 모르겠는 그런..
    private final UserRepository userRepository;
    private final SpacePostRepository savePostRepository;

//    public Long addCoursePost(){
//
//    }


}
