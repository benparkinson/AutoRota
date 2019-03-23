import { combineReducers } from 'redux';
import { reducer as formReducer } from 'redux-form';
import rotaReducer from './rotaReducer';

// keys for this object will end up in state!
export default combineReducers({
    rota: rotaReducer,
    form: formReducer
});