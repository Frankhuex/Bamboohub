import BookFilterPageWithURL from "./BookFilterPageWithURL";

export default function Plaza() {
    return (<>
        <h1 className="text-xl mt-5 flex justify-center">这里陈列了公开阅读或编辑的书。</h1>
        <BookFilterPageWithURL url={"plaza"} defaultClassifiedBy='scope' defaultSortedBy="title" />
        </>
    )
}