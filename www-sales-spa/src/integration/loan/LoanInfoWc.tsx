import React from "react";

export const LoanInfoWc = (props: {id: string, sourceId: string | undefined, sourceVersion: number | undefined}) => {
    const {sourceId} = props
    const {sourceVersion} = props

    if (sourceId === undefined || sourceVersion === undefined) {
        return null
    }

    const {id} = props
    return (
        <loan-info
            id={id}
            className="alert alert-success"
            sourceId={sourceId}
            sourceVersion={sourceVersion}
        />
    )
}