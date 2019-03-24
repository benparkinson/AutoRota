import { combineReducers } from 'redux';
import { reducer as formReducer } from 'redux-form';
import rotaReducer from './rotaReducer';
import ruleReducer from './ruleReducer';

// keys for this object will end up in state!
export default combineReducers({
    rotas: rotaReducer,
    rules: ruleReducer,
    form: formReducer
});