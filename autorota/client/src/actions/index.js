import { CREATE_ROTA, VIEW_ROTA, FETCH_HARD_RULES, FETCH_SOFT_RULES } from './types';
import rotas from '../apis/rotas';

export const createRota = (formValues) => async dispatch => {
    const response = await rotas.post('/rotas/create', formValues);

    const updateTime = new Date();
    dispatch({
        type: CREATE_ROTA,
        payload: `${updateTime}: ${response.data}`
    });
}

export const viewRota = () => async dispatch => {
    const response = await rotas.get('/rotas/');

    dispatch({
        type: VIEW_ROTA,
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