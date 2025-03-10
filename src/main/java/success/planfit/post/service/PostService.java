package success.planfit.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import success.planfit.repository.UserRepository;

@Transactional
@RequiredArgsConstructor
@Service
public class PostService {


    private final UserRepository userRepository;

//    public Long addCoursePost(){
//
//    }


}
