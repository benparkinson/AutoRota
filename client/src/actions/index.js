import {
    CREATE_ROTA,
    FETCH_ROTA,
    FETCH_HARD_RULES,
    FETCH_SOFT_RULES,
    FETCH_ROTAS
} from './types';
import rotas from '../apis/rotas';
import history from '../history';

export const createRota = (formValues) => async dispatch => {
    const response = await rotas.post('/rotas/create', formValues);

    dispatch({
        type: CREATE_ROTA,
        payload: response.data
    });
    history.push('/rotas');
}

export const fetchRotas = () => async dispatch => {
    const response = await rotas.get('/rotas/');

    dispatch({
        type: FETCH_ROTAS,
        payload: response.data._embedded.rotas
    });
}

export const fetchRota = (rotaId) => async dispatch => {
    const response = await rotas.get(`/rotas/${rotaId}`);

    dispatch({
        type: FETCH_ROTA,
        payload: response.data
    });
}

export const fetchRotaHardRules = () => async dispatch => {
    const response = await rotas.get('/rules/hard/get');

    dispatch({ type: FETCH_HARD_RULES, payload: response.data });
}

export const fetchRotaSoftRules = () => async dispatch => {
    const response = await rotas.get('/rules/soft/get');

    dispatch({ type: FETCH_SOFT_RULES, payload: response.data });
}