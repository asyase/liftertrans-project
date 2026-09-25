import axios from 'axios';

export default  {

 getVehiclesRequest() {
   return axios.get('/api/vehicles');
 }
}


