import React from 'react';
import { Router, Route, Switch } from 'react-router-dom';
import RotaCreate from './rotaCreate/RotaCreate';
import RotaView from './rotaView/RotaView';
import Header from './Header';
import Home from './Home';
import history from '../history';

const App = () => {
    return (
        <div className="ui container">
            <Router history={history}>
                <div>
                    <Header />
                    <Switch>
                        <Route path="/" exact component={Home} />
                        <Route path="/rotas/new" exact component={RotaCreate} />
                        <Route path="/rotas/view" exact component={RotaView} />
                    </Switch>
                </div>
            </Router>
        </div>
    );
}

export default App;