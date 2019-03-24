import React from 'react';
import { Router, Route, Switch } from 'react-router-dom';
import { Helmet } from 'react-helmet';

import RotaCreate from './rotaCreate/RotaCreate';
import RotaView from './rotaView/RotaView';
import RotaList from './rotaView/RotaList';
import Header from './Header';
import Home from './Home';
import history from '../history';

const App = () => {
    return (
        <div className="ui container">
            <Helmet>
                <style>{'body { background-color: #d7e2f7; }'}</style>
            </Helmet>
            <Router history={history}>
                <div>
                    <Header />
                    <Switch>
                        <Route path="/" exact component={Home} />
                        <Route path="/rotas/new" exact component={RotaCreate} />
                        <Route path="/rotas/" exact component={RotaList} />
                        <Route path="/rotas/:id" exact component={RotaView} />
                    </Switch>
                </div>
            </Router>
        </div>
    );
}

export default App;