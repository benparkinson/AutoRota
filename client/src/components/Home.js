import React from 'react';
import { Link } from 'react-router-dom';

const Home = () => {
    return (
        <div className="ui center aligned container">
            <Link to="/rotas/new" className="ui button primary">
                Create New Rota
            </Link>
            <Link to="/rotas/" className="ui button primary">
                View Rotas
            </Link>
        </div>
    );
}

export default Home;