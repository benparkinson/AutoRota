import React from 'react';

const FluidCard = (props) => {
    return (
        <div className="ui fluid card">
            <div className="content">
                {props.children}
            </div>
        </div>
    );
}

export default FluidCard;