import React from 'react';
import { Link } from 'react-router-dom';

const Home = () => {
    return (
        <div className="ui center aligned container">
            <Link to="/rotas/new" className="ui button primary">
                Create New Rota
            </Link>
            <Link to="/rotas/view" className="ui button primary">
                View Latest Rota
            </Link>
        </div>
    );
}

export default Home;