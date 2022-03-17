package com.cos.blog.test;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cos.blog.model.RoleType;
import com.cos.blog.model.User;
import com.cos.blog.repository.UserRepository;

import net.bytebuddy.asm.Advice.OffsetMapping.Sort;

@RestController	//데이터x, 응답만 할 것.
public class DummyControllerTest {
	
		@Autowired	//의존성 주입
		private UserRepository userRepository;
		
		//select 테스트
		
		//전체 회원 조회
		@GetMapping("/dummy/users")
		public List<User> list(){
			return userRepository.findAll();
		}
		
		//한 페이지당 2건의 데이터를 리턴받아 보기
		/*
		 * @GetMapping("/dummy/user/page") //한 페이지당 2건씩, id기준, 최근에 가입한 순으로 조회 public
		 * Page<User> pageList(@PageableDefault(size = 2,sort = "id",direction =
		 * Direction.DESC) Pageable pageable){ Page<User> users=
		 * userRepository.findAll(pageable); return users; }
		 */
		
		@GetMapping("/dummy/user")					//한 페이지당 2건씩, id기준, 최근에 가입한 순으로 조회
		public List<User> pageList(@PageableDefault(size = 2,sort = "id",direction = Direction.DESC) Pageable pageable){
			Page<User> pagingUser= userRepository.findAll(pageable);
			
			//결과는 List로 받는 것이 좋음.
			List<User> users = pagingUser.getContent();
			return users;
		}

		//특정 회원 조회
		//{id}주소로 파라미터를 전달받을 수 있음.
		@GetMapping("/dummy/user/{id}")
		public User detail(@PathVariable int id) {
			//user/4를 찾으면 내가 데이터베이스에서 못찾게 되면 user가 null이 될 것 아냐?
			//그럼 return null이 되니까 Optional로 너의 User객체를 감싸서 가져올테니 null인지 아닌지 판단해서 return해!
			User user =  userRepository.findById(id).orElseThrow(new Supplier<IllegalArgumentException>() {

				@Override
				public IllegalArgumentException get() {
					// TODO Auto-generated method stub
					return new IllegalArgumentException("해당 유저는 없습니다. id: "+id);
				}
			
			});
			//요청 : 웹 브라우저
			//user 객체 = 자바 오브젝트
			//변환(웹 브라우저가 이해할 수 있는 데이터) -> json
			//스프링부트는 MessageConverter라는 애가 응답 시 자동 작동함.
			//만약에 자바 오브젝트를 리턴하게 되면 MessageConverter가 Jackson 라이브러리를 호출해서
			//user 오브젝트를 json으로 변환해서 브라우저에게 던져줍니다.
			return user;
		}
		
		/*
		 * save 함수는 id를 전달하지 않으면 insert를 해주고
		 * id를 전달하면 해당 id에 대한 데이터가 있으면 update를 해주고
		 * 해당 id에 대한 데이터가 없으면 insert를 함.
		 */
		
		//update 테스트
		//email, password
		@Transactional
		@PutMapping("/dummy/user/{id}")				//json 데이터로 받기
		public User updateUser(@PathVariable int id, @RequestBody User requestUser) {	//json데이터를 요청=>스프링이(MessageConverter의 Jackson라이브러리 작동)
																																				//Java Object로 변환해서 받아줌.
			System.out.println("id: "+id);
			System.out.println("password: "+requestUser.getPassword());
			System.out.println("email: "+requestUser.getEmail());
			
			User user = userRepository.findById(id).orElseThrow(()->{
				return new IllegalArgumentException("수정에 실패하였습니다.");
			});
			
			user.setPassword(requestUser.getPassword());
			user.setEmail(requestUser.getEmail());

			//save를 사용하지 않고 트랜젝션을 사용하면 update 가능
			//데이터베이스에서 select를 해서(findById) 받아와서 그 객체의 값만 변경하고 위에 트랜젝션만 걸면 된다.
			//userRepository.save(user);
			
			//더티 체킹
			
			return user;
		}
		
		//delete 테스트
		
		@DeleteMapping("/dummy/user/{id}")
		public String  delete(@PathVariable int id) {
			
			try {
				userRepository.deleteById(id);
			}catch (EmptyResultDataAccessException e) {
				return "삭제에 실패했습니다. 해당 id는 DB에 없습니다.";
			}
			
			
			return "삭제되었습니다. id: "+id;
		}
	
		@PostMapping("/dummy/join")
		public String join(User user) {	//key-value : 약속된 규칙
			
			System.out.println("id: "+user.getId());
			System.out.println("username: "+user.getUsername());
			System.out.println("password: "+user.getPassword());
			System.out.println("email: "+user.getEmail());
			System.out.println("role: "+user.getRole());
			System.out.println("createDate: "+user.getCreateDate());
			
			user.setRole(RoleType.USER);
			userRepository.save(user);
			
			return "회원 가입이 완료되었습니다.";
		}
	}
