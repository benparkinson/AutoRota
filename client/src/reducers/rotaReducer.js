import { CREATE_ROTA, FETCH_HARD_RULES, FETCH_SOFT_RULES, VIEW_ROTA } from '../actions/types';

export default (state = {}, action) => {
    switch (action.type) {
        case CREATE_ROTA:
            return { ...state, rotaSubmitMessage: action.payload };
        case VIEW_ROTA:
            return { ...state, latestRota: action.payload };
        case FETCH_HARD_RULES:
            return { ...state, hardRules: action.payload };
        case FETCH_SOFT_RULES:
            return { ...state, softRules: action.payload };
        default:
            return state;
    }
}