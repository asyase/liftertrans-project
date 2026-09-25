import axios  from 'axios';

export default {
  postLoginRequest(authRequestDto){
    return axios.post('/api/auth/login',authRequestDto
    );
  }
}
