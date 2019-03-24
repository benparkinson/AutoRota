import React from 'react';
import { Link } from 'react-router-dom';

const Home = () => {
    return (
        <div className="ui center aligned grid container">
            <div className="row"></div>
            <div className="row"></div>
            <div className="row">
                <h2>Welcome to AutoRota!</h2>
            </div>
            <div className="row">
                <Link to="/rotas/new" className="ui button primary">
                    Create New Rota
                </Link>
            </div>
            <div className="row">
                <Link to="/rotas/" className="ui button primary">
                    View Rotas
                </Link>
            </div>
        </div>
    );
}

export default Home;