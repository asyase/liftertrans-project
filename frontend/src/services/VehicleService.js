import axios from 'axios';

export default  {

 // Kõik autod (koos status ja subcontractorId väljaga)
 getVehiclesRequest() {
   return axios.get('/api/vehicles');
 }
}


