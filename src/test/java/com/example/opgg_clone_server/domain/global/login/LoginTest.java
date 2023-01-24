package com.example.opgg_clone_server.domain.global.login;

import com.example.opgg_clone_server.domain.member.Member;
import com.example.opgg_clone_server.domain.member.Role;
import com.example.opgg_clone_server.domain.member.exception.MemberException;
import com.example.opgg_clone_server.domain.member.exception.MemberExceptionType;
import com.example.opgg_clone_server.domain.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class LoginTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    PasswordEncoder delegatingPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    ObjectMapper objectMapper = new ObjectMapper();

    private static String KEY_USERNAME = "username";
    private static String KEY_PASSWORD = "password";
    private static String USERNAME = "username";
    private static String PASSWORD = "123456789";

    private static String LOGIN_URL = "/login";

    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static String LOGIN_FAIL_MESSAGE = "fail";

    private void clear() {
        em.flush();
        em.clear();
    }


    @BeforeEach
    private void init() {
        memberRepository.save(Member.builder()
                .username(USERNAME)
                .password(delegatingPasswordEncoder.encode(PASSWORD))
                .name("Member1")
                .nickName("NickName1")
                .role(Role.USER)
                .age(22)
                .build());
        clear();
    }

    private Map getUsernamePasswordMap(String username, String password) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(KEY_USERNAME, username);
        map.put(KEY_PASSWORD, password);
        return map;
    }


    private ResultActions perform(String url, MediaType mediaType, Map usernamePasswordMap) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .post(url)
                .contentType(mediaType)
                .content(objectMapper.writeValueAsString(usernamePasswordMap)));

    }

    @Test
    public void 로그인_성공() throws Exception {
        //given
        Map<String, String> map = getUsernamePasswordMap(USERNAME, PASSWORD);


        //when, then
        MvcResult result = perform(LOGIN_URL, APPLICATION_JSON, map)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Member member = memberRepository.findByUsername(USERNAME).orElseThrow(()-> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));

        assertThat(member.getRefreshToken()).isNotNull();
    }

    @Test
    public void 로그인_실패_아이디틀림() throws Exception {
        //given
        Map<String, String> map = new HashMap<>();
        map.put("username",USERNAME+"123");
        map.put("password",PASSWORD);


        //when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(map)))
                .andDo(print())
                //.andExpect(status().isOk())//TODO 상테코드변경
                .andExpect(status().isBadRequest())
                .andReturn();

        //then
        assertThat(result.getResponse().getHeader(accessHeader)).isNull();
        assertThat(result.getResponse().getHeader(refreshHeader)).isNull();

    }

    @Test
    public void 로그인_실패_비밀번호틀림() throws Exception {
        //given
        Map<String, String> map = new HashMap<>();
        map.put("username",USERNAME);
        map.put("password",PASSWORD+"123");


        //when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(map)))
                .andDo(print())
                //.andExpect(status().isOk())//TODO 상태코드변경
                .andExpect(status().isBadRequest())
                .andReturn();


        //then
        assertThat(result.getResponse().getHeader(accessHeader)).isNull();
        assertThat(result.getResponse().getHeader(refreshHeader)).isNull();

    }

    @Test
    public void 로그인_주소가_틀리면_FORBIDDEN() throws Exception {
        //given
        Map<String, String> map = getUsernamePasswordMap(USERNAME, PASSWORD);


        //when, then
        perform(LOGIN_URL +"123", APPLICATION_JSON, map)
                .andDo(print())
                .andExpect(status().isForbidden());

    }

    // 로그인_데이터형식_JSON이_아니면_200
    @Test
    public void 로그인_데이터형식_JSON이_아니면_400() throws Exception {

        //given
        Map<String, String> map = new HashMap<>();
        map.put("username",USERNAME);
        map.put("password",PASSWORD);


        //when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(objectMapper.writeValueAsString(map)))
                .andDo(print())
                //.andExpect(status().isOk())  TODO 상태코드변겅
                .andExpect(status().isBadRequest())
                .andReturn();

        //then
        assertThat(result.getResponse().getContentAsString()).isEqualTo(LOGIN_FAIL_MESSAGE);
    }

    @Test
    public void 로그인_HTTP_METHOD_GET이면_NOTFOUND() throws Exception {
        //given
        Map<String, String> map = getUsernamePasswordMap(USERNAME, PASSWORD);


        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .get(LOGIN_URL)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .content(objectMapper.writeValueAsString(map)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
    @Test
    public void 오류_로그인_HTTP_METHOD_PUT이면_NOTFOUND() throws Exception {
        //given
        Map<String, String> map = getUsernamePasswordMap(USERNAME, PASSWORD);

        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .put(LOGIN_URL)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .content(objectMapper.writeValueAsString(map)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

}