import _ from 'lodash';

import {
    CREATE_ROTA,
    FETCH_ROTA,
    FETCH_ROTAS
} from '../actions/types';

export default (state = {}, action) => {
    switch (action.type) {
        case CREATE_ROTA:
            return { ...state, [action.payload.id]: action.payload };
        case FETCH_ROTA:
            return { ...state, [action.payload.id]: action.payload };
        case FETCH_ROTAS:
            return { ...state, ..._.mapKeys(action.payload, 'id') };
        default:
            return state;
    }
}